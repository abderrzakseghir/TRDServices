import { useEffect, useState } from 'react';
import {
  Box,
  Grid,
  Typography,
  Button,
  TextField,
  MenuItem,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Chip,
  CircularProgress,
  Alert,
  Snackbar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { footballApi } from '../services/api';
import { Match, Team } from '../types/football';

export default function Matches() {
  const [matches, setMatches] = useState<Match[]>([]);
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });

  // Form state
  const [homeTeamId, setHomeTeamId] = useState<number>(0);
  const [awayTeamId, setAwayTeamId] = useState<number>(0);
  const [matchDate, setMatchDate] = useState<string>('');
  const [matchTime, setMatchTime] = useState<string>('20:00');
  const [matchday, setMatchday] = useState<number>(1);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [matchesData, teamsData] = await Promise.all([
        footballApi.getMatches(),
        footballApi.getTeams(),
      ]);
      setMatches(matchesData.matches);
      setTeams(teamsData.teams);
    } catch (error) {
      console.error('Error loading data:', error);
      showMessage('Erreur lors du chargement des données', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showMessage = (message: string, severity: 'success' | 'error') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCreateMatch = async () => {
    if (!homeTeamId || !awayTeamId || !matchDate) {
      showMessage('Veuillez remplir tous les champs', 'error');
      return;
    }

    if (homeTeamId === awayTeamId) {
      showMessage('Les équipes doivent être différentes', 'error');
      return;
    }

    try {
      const utcDate = new Date(`${matchDate}T${matchTime}:00`).toISOString();
      await footballApi.createMatch({
        homeTeamId,
        awayTeamId,
        utcDate,
        matchday,
      });
      showMessage('Match créé avec succès', 'success');
      setDialogOpen(false);
      resetForm();
      loadData();
    } catch (error) {
      console.error('Error creating match:', error);
      showMessage('Erreur lors de la création du match', 'error');
    }
  };

  const handleDeleteMatch = async (matchId: number) => {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce match ?')) {
      return;
    }

    try {
      await footballApi.deleteMatch(matchId);
      showMessage('Match supprimé avec succès', 'success');
      loadData();
    } catch (error) {
      console.error('Error deleting match:', error);
      showMessage('Erreur lors de la suppression du match', 'error');
    }
  };

  const resetForm = () => {
    setHomeTeamId(0);
    setAwayTeamId(0);
    setMatchDate('');
    setMatchTime('20:00');
    setMatchday(1);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'SCHEDULED': return 'info';
      case 'IN_PLAY': return 'success';
      case 'FINISHED': return 'default';
      case 'POSTPONED': return 'warning';
      case 'CANCELLED': return 'error';
      default: return 'default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'SCHEDULED': return 'Programmé';
      case 'IN_PLAY': return 'En cours';
      case 'FINISHED': return 'Terminé';
      case 'POSTPONED': return 'Reporté';
      case 'CANCELLED': return 'Annulé';
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

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Gestion des Matchs</Typography>
        <Box>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={loadData}
            sx={{ mr: 2 }}
          >
            Actualiser
          </Button>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setDialogOpen(true)}
          >
            Nouveau Match
          </Button>
        </Box>
      </Box>

      {matches.length === 0 ? (
        <Alert severity="info">
          Aucun match disponible. Créez votre premier match !
        </Alert>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Équipe Domicile</TableCell>
                <TableCell>Équipe Extérieur</TableCell>
                <TableCell align="center">Score</TableCell>
                <TableCell>Statut</TableCell>
                <TableCell>Journée</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {matches.map((match) => (
                <TableRow key={match.id}>
                  <TableCell>{match.id}</TableCell>
                  <TableCell>
                    {new Date(match.utcDate).toLocaleString('fr-FR', {
                      dateStyle: 'short',
                      timeStyle: 'short',
                    })}
                  </TableCell>
                  <TableCell>
                    <Box display="flex" alignItems="center" gap={1}>
                      {match.homeTeam.crest && (
                        <img src={match.homeTeam.crest} alt="" style={{ width: 24, height: 24 }} />
                      )}
                      {match.homeTeam.name}
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Box display="flex" alignItems="center" gap={1}>
                      {match.awayTeam.crest && (
                        <img src={match.awayTeam.crest} alt="" style={{ width: 24, height: 24 }} />
                      )}
                      {match.awayTeam.name}
                    </Box>
                  </TableCell>
                  <TableCell align="center">
                    <Typography variant="h6">
                      {match.score.fullTime.home ?? '-'} - {match.score.fullTime.away ?? '-'}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={getStatusLabel(match.status)}
                      color={getStatusColor(match.status)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>{match.matchday}</TableCell>
                  <TableCell align="center">
                    <IconButton
                      color="error"
                      onClick={() => handleDeleteMatch(match.id)}
                      size="small"
                    >
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Create Match Dialog */}
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Créer un nouveau match</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                select
                fullWidth
                label="Équipe Domicile"
                value={homeTeamId}
                onChange={(e) => setHomeTeamId(Number(e.target.value))}
              >
                <MenuItem value={0}>Sélectionner une équipe</MenuItem>
                {teams.map((team) => (
                  <MenuItem key={team.id} value={team.id} disabled={team.id === awayTeamId}>
                    {team.name}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                select
                fullWidth
                label="Équipe Extérieur"
                value={awayTeamId}
                onChange={(e) => setAwayTeamId(Number(e.target.value))}
              >
                <MenuItem value={0}>Sélectionner une équipe</MenuItem>
                {teams.map((team) => (
                  <MenuItem key={team.id} value={team.id} disabled={team.id === homeTeamId}>
                    {team.name}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                type="date"
                label="Date du match"
                value={matchDate}
                onChange={(e) => setMatchDate(e.target.value)}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                type="time"
                label="Heure du match"
                value={matchTime}
                onChange={(e) => setMatchTime(e.target.value)}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                type="number"
                label="Journée"
                value={matchday}
                onChange={(e) => setMatchday(Number(e.target.value))}
                inputProps={{ min: 1, max: 38 }}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Annuler</Button>
          <Button variant="contained" onClick={handleCreateMatch}>
            Créer
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
