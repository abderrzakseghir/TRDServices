import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import redis.asyncio as redis
from typing import List, Dict
from src.config import settings
from src.mock_data import UPCOMING_MATCHES

class HybridEngine:
    def __init__(self):
        # Async Redis Connection
        self.redis = redis.from_url(settings.REDIS_URL, decode_responses=True)

    async def get_user_vector(self, user_id: str):
        # Returns dict: {'Lakers': '5', 'NBA': '3'}
        data = await self.redis.hgetall(f"user:{user_id}:tags")
        # Convert strings to integers
        return {k: int(v) for k, v in data.items()}

    async def recommend(self, user_id: str) -> List[Dict]:
        """
        The Main Logic:
        1. Fetch User Profile.
        2. Build Vector Universe (User Tags + Match Tags).
        3. Calculate Cosine Similarity.
        4. Apply Global Trend Weights.
        """
        user_tags_map = await self.get_user_vector(user_id)
        
        # --- COLD START HANDLING ---
        # If user has no history, return generic list (skipping math)
        if not user_tags_map:
            return UPCOMING_MATCHES[:5]

        # 1. Build the "Universe" of Tags
        # (All unique tags involved in this calculation)
        match_tags_set = set()
        for m in UPCOMING_MATCHES:
            match_tags_set.update(m['tags'])
        
        universe = list(set(user_tags_map.keys()) | match_tags_set)
        
        # 2. Create User Vector
        # Example: [5, 0, 2, 1...] based on universe order
        user_vector = [user_tags_map.get(tag, 0) for tag in universe]
        user_vector_np = np.array(user_vector).reshape(1, -1)

        # 3. Fetch Global Trend Data (for normalization)
        # We need the max score to normalize between 0.0 and 1.0
        # In prod, cache this value to avoid a heavy DB call every time
        all_trend_scores = await self.redis.zrange("global:trends", 0, -1, withscores=True)
        max_trend_score = 1
        if all_trend_scores:
            max_trend_score = max([s for _, s in all_trend_scores])

        recommendations = []

        # 4. Score Each Match
        for match in UPCOMING_MATCHES:
            # --- A. Content Score (Cosine) ---
            match_vector = [1 if tag in match['tags'] else 0 for tag in universe]
            match_vector_np = np.array(match_vector).reshape(1, -1)
            
            # Result is [[0.85]]
            sim_score = cosine_similarity(user_vector_np, match_vector_np)[0][0]
            
            # --- B. Trend Score (Social) ---
            match_trend_total = 0
            # Pipeline reads for speed
            async with self.redis.pipeline() as pipe:
                for tag in match['tags']:
                    pipe.zscore("global:trends", tag)
                results = await pipe.execute()
            
            # Sum up popularity of all tags in this match
            current_match_score = sum([float(r) for r in results if r])
            norm_trend_score = current_match_score / max_trend_score if max_trend_score > 0 else 0
            
            # --- C. Final Hybrid Weighting ---
            final_score = (sim_score * settings.WEIGHT_CONTENT) + \
                          (norm_trend_score * settings.WEIGHT_TREND)
            
            recommendations.append({
                "match": match['title'],
                "score": round(final_score, 4),
                "debug": {
                    "cosine": round(sim_score, 2),
                    "trend": round(norm_trend_score, 2)
                }
            })

        # 5. Sort Descending
        recommendations.sort(key=lambda x: x['score'], reverse=True)
        return recommendations[:5]

    async def close(self):
        await self.redis.aclose()