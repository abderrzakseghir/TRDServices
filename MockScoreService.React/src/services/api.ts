import axios from 'axios';
import { MatchesResponse, TeamsResponse, CreateMatchRequest, CreateTeamRequest } from '../types/football';

// In production (Docker), use empty string so nginx proxies /v4 to the API
// In development, use localhost:5000
const API_BASE_URL = import.meta.env.VITE_API_URL || '';

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

  createMatch: async (match: CreateMatchRequest) => {
    const response = await api.post('/v4/matches', match);
    return response.data;
  },

  updateMatchScore: async (matchId: number, homeScore: number, awayScore: number) => {
    const response = await api.patch(`/v4/matches/${matchId}/score`, {
      homeScore,
      awayScore,
    });
    return response.data;
  },

  updateMatchStatus: async (matchId: number, status: string) => {
    const response = await api.patch(`/v4/matches/${matchId}/status`, {
      status,
    });
    return response.data;
  },

  deleteMatch: async (matchId: number) => {
    const response = await api.delete(`/v4/matches/${matchId}`);
    return response.data;
  },

  // Teams
  getTeams: async () => {
    const response = await api.get<TeamsResponse>('/v4/teams');
    return response.data;
  },

  createTeam: async (team: CreateTeamRequest) => {
    const response = await api.post('/v4/teams', team);
    return response.data;
  },

  deleteTeam: async (teamId: number) => {
    const response = await api.delete(`/v4/teams/${teamId}`);
    return response.data;
  },
};

export default api;
