import { useEffect, useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  CardActions,
  Grid,
  Typography,
  Button,
  IconButton,
  Chip,
  CircularProgress,
  Alert,
  Snackbar,
} from '@mui/material';
import {
  Add as AddIcon,
  Remove as RemoveIcon,
  PlayArrow,
  Stop,
  Refresh,
  Replay,
} from '@mui/icons-material';
import { footballApi } from '../services/api';
import { Match } from '../types/football';

export default function Simulation() {
const [matches, setMatches] = useState<Match[]>([]);
const [loading, setLoading] = useState(true);
const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' | 'info' });

useEffect(() => {
  loadMatches();
    
  // Auto-refresh every 2 seconds
  const interval = setInterval(loadMatches, 2000);
  return () => clearInterval(interval);
}, []);

const loadMatches = async () => {
try {
  const data = await footballApi.getMatches();
  // Sort: IN_PLAY first, then SCHEDULED, then FINISHED
  const sorted = data.matches.sort((a, b) => {
    const order: Record<string, number> = { IN_PLAY: 0, SCHEDULED: 1, TIMED: 1, PAUSED: 1, FINISHED: 2, POSTPONED: 3, CANCELLED: 3 };
    return (order[a.status] ?? 3) - (order[b.status] ?? 3);
  });
  setMatches(sorted);
} catch (error) {
  console.error('Error loading matches:', error);
  } finally {
    setLoading(false);
  }
};

const showMessage = (message: string, severity: 'success' | 'error' | 'info' = 'success') => {
  setSnackbar({ open: true, message, severity });
};

const handleScoreChange = async (matchId: number, isHome: boolean, delta: number) => {
  try {
    const match = matches.find(m => m.id === matchId);
    if (!match) return;
    
    const currentHomeScore = match.score.fullTime.home ?? 0;
    const currentAwayScore = match.score.fullTime.away ?? 0;
    
    const newHomeScore = isHome ? Math.max(0, currentHomeScore + delta) : currentHomeScore;
    const newAwayScore = !isHome ? Math.max(0, currentAwayScore + delta) : currentAwayScore;
    
    await footballApi.updateMatchScore(matchId, newHomeScore, newAwayScore);
    showMessage(`Score ${delta > 0 ? 'augmenté' : 'diminué'}`, 'success');
    await loadMatches();
  } catch (error) {
    showMessage('Erreur lors de la modification du score', 'error');
  }
};

const handleStartMatch = async (matchId: number) => {
  try {
    await footballApi.updateMatchStatus(matchId, 'IN_PLAY');
    showMessage('Match démarré', 'success');
    await loadMatches();
  } catch (error) {
    showMessage('Erreur lors du démarrage du match', 'error');
  }
};

const handleFinishMatch = async (matchId: number) => {
  try {
    await footballApi.updateMatchStatus(matchId, 'FINISHED');
    showMessage('Match terminé', 'info');
    await loadMatches();
  } catch (error) {
    showMessage('Erreur lors de la fin du match', 'error');
  }
};

const handleResetScore = async (matchId: number) => {
  try {
    await footballApi.updateMatchScore(matchId, 0, 0);
    showMessage('Scores remis à zéro', 'info');
    await loadMatches();
  } catch (error) {
    showMessage('Erreur lors du reset', 'error');
  }
};

const handleRestartMatch = async (matchId: number) => {
  try {
    await footballApi.updateMatchScore(matchId, 0, 0);
    await footballApi.updateMatchStatus(matchId, 'SCHEDULED');
    showMessage('Match réinitialisé', 'info');
    await loadMatches();
  } catch (error) {
    showMessage('Erreur lors du redémarrage', 'error');
  }
};

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'SCHEDULED': return 'info';
      case 'IN_PLAY': return 'success';
      case 'FINISHED': return 'default';
      default: return 'default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'SCHEDULED': return 'Programmé';
      case 'IN_PLAY': return 'En cours';
      case 'FINISHED': return 'Terminé';
      default: return status;
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  if (matches.length === 0) {
    return (
      <Box>
        <Typography variant="h4" gutterBottom>
          Simulation Temps Réel
        </Typography>
        <Alert severity="info" sx={{ mt: 2 }}>
          Aucun match disponible pour la simulation. <a href="/matches">Créer un match</a>
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Simulation Temps Réel
      </Typography>
      <Typography variant="body1" color="textSecondary" sx={{ mb: 3 }}>
        Contrôlez les scores des matchs en cours en temps réel
      </Typography>

      <Grid container spacing={3}>
        {matches.map((match) => (
          <Grid item xs={12} md={6} lg={4} key={match.id}>
            <Card>
              <CardContent>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                  <Typography variant="h6" component="div" noWrap>
                    {match.homeTeam.shortName} vs {match.awayTeam.shortName}
                  </Typography>
                  <Chip
                    label={getStatusLabel(match.status)}
                    color={getStatusColor(match.status)}
                    size="small"
                  />
                </Box>
                
                <Typography variant="body2" color="textSecondary" gutterBottom>
                  {new Date(match.utcDate).toLocaleString('fr-FR')}
                </Typography>

                {/* Home Team */}
                <Box
                  display="flex"
                  justifyContent="space-between"
                  alignItems="center"
                  my={2}
                  p={2}
                  bgcolor="background.paper"
                  borderRadius={1}
                >
                  <Typography variant="h6" sx={{ flex: 1 }}>
                    {match.homeTeam.name}
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1}>
                    <IconButton
                      size="small"
                      color="error"
                      disabled={match.status !== 'IN_PLAY' || (match.score.fullTime.home ?? 0) === 0}
                      onClick={() => handleScoreChange(match.id, true, -1)}
                    >
                      <RemoveIcon />
                    </IconButton>
                    <Typography variant="h4" sx={{ minWidth: 40, textAlign: 'center' }}>
                      {match.score.fullTime.home ?? 0}
                    </Typography>
                    <IconButton
                      size="small"
                      color="success"
                      disabled={match.status !== 'IN_PLAY'}
                      onClick={() => handleScoreChange(match.id, true, 1)}
                    >
                      <AddIcon />
                    </IconButton>

                  </Box>
                </Box>

                {/* Away Team */}
                <Box
                  display="flex"
                  justifyContent="space-between"
                  alignItems="center"
                  my={2}
                  p={2}
                  bgcolor="background.paper"
                  borderRadius={1}
                >
                  <Typography variant="h6" sx={{ flex: 1 }}>
                    {match.awayTeam.name}
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1}>
                    <IconButton
                      size="small"
                      color="error"
                      disabled={match.status !== 'IN_PLAY' || (match.score.fullTime.away ?? 0) === 0}
                      onClick={() => handleScoreChange(match.id, false, -1)}
                    >
                      <RemoveIcon />
                    </IconButton>
                    <Typography variant="h4" sx={{ minWidth: 40, textAlign: 'center' }}>
                      {match.score.fullTime.away ?? 0}
                    </Typography>
                    <IconButton
                      size="small"
                      color="success"
                      disabled={match.status !== 'IN_PLAY'}
                      onClick={() => handleScoreChange(match.id, false, 1)}
                    >
                      <AddIcon />
                    </IconButton>

                  </Box>
                </Box>
              </CardContent>

              <CardActions>
                {match.status === 'SCHEDULED' && (
                  <Button
                    size="small"
                    variant="contained"
                    color="success"
                    startIcon={<PlayArrow />}
                    onClick={() => handleStartMatch(match.id)}
                  >
                    Démarrer
                  </Button>
                )}
                {match.status === 'IN_PLAY' && (
                  <>
                    <Button
                      size="small"
                      variant="contained"
                      color="warning"
                      startIcon={<Stop />}
                      onClick={() => handleFinishMatch(match.id)}
                    >
                      Terminer
                    </Button>
                    <Button
                      size="small"
                      variant="outlined"
                      startIcon={<Refresh />}
                      onClick={() => handleResetScore(match.id)}
                    >
                      Reset
                    </Button>
                  </>
                )}
                {match.status === 'FINISHED' && (
                  <Button
                    size="small"
                    variant="outlined"
                    startIcon={<Replay />}
                    onClick={() => handleRestartMatch(match.id)}
                  >
                    Rejouer
                  </Button>
                )}
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        message={snackbar.message}
      />
    </Box>
  );
}
