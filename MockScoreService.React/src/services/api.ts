import axios from 'axios';
import { MatchesResponse, Team } from '../types/football';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:5000';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const footballApi = {
  // Matches
  getMatches: async (status?: string) => {
    const params = status ? { status } : {};
    const response = await api.get<MatchesResponse>('/v4/matches', { params });
    return response.data;
  },

  getMatch: async (id: number) => {
    const response = await api.get(`/v4/matches/${id}`);
    return response.data;
  },

  // Teams (via direct DB access in web app)
  getTeams: async () => {
    // This would need a new endpoint in the API
    // For now, we extract from matches
    return [];
  },
};

// For direct database operations (Web app only)
export const dbApi = {
  createMatch: async (match: any) => {
    // This would call the Web app's API endpoint
    const response = await axios.post('http://localhost:5001/api/matches', match);
    return response.data;
  },

  updateMatchScore: async (matchId: number, homeScore: number, awayScore: number) => {
    const response = await axios.patch(`http://localhost:5001/api/matches/${matchId}/score`, {
      homeScore,
      awayScore,
    });
    return response.data;
  },

  updateMatchStatus: async (matchId: number, status: string) => {
    const response = await axios.patch(`http://localhost:5001/api/matches/${matchId}/status`, {
      status,
    });
    return response.data;
  },

  deleteMatch: async (matchId: number) => {
    await axios.delete(`http://localhost:5001/api/matches/${matchId}`);
  },

  createTeam: async (team: Partial<Team>) => {
    const response = await axios.post('http://localhost:5001/api/teams', team);
    return response.data;
  },

  deleteTeam: async (teamId: number) => {
    await axios.delete(`http://localhost:5001/api/teams/${teamId}`);
  },
};

export default api;
