export interface Team {
  id: number;
  name: string;
  shortName: string;
  tla: string;
  crest?: string;
}

export interface Competition {
  id: number;
  name: string;
  code: string;
  type: string;
  emblem: string;
}

export interface Season {
  id: number;
  startDate: string;
  endDate: string;
  currentMatchday: number;
  winner?: string;
}

export interface ScoreDetail {
  home: number | null;
  away: number | null;
}

export interface Score {
  winner: string | null;
  duration: string;
  fullTime: ScoreDetail;
  halfTime: ScoreDetail;
}

export interface Match {
  id: number;
  utcDate: string;
  status: 'SCHEDULED' | 'IN_PLAY' | 'FINISHED';
  matchday: number;
  stage: string;
  group?: string;
  lastUpdated: string;
  homeTeam: Team;
  awayTeam: Team;
  score: Score;
  competition: Competition;
  season: Season;
}

export interface MatchesResponse {
  filters: {
    status?: string;
    season?: string;
  };
  resultSet: {
    count: number;
    first: string;
    last: string;
    played: number;
  };
  competition: Competition;
  matches: Match[];
}
