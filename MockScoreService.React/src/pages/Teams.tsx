import { useEffect, useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  Button,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
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
  Avatar,
} from '@mui/material';
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Refresh as RefreshIcon,
  Groups as GroupsIcon,
} from '@mui/icons-material';
import { footballApi } from '../services/api';
import { Team } from '../types/football';

export default function Teams() {
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });

  // Form state
  const [name, setName] = useState('');
  const [shortName, setShortName] = useState('');
  const [tla, setTla] = useState('');
  const [crest, setCrest] = useState('');

  useEffect(() => {
    loadTeams();
  }, []);

  const loadTeams = async () => {
    setLoading(true);
    try {
      const data = await footballApi.getTeams();
      setTeams(data.teams);
    } catch (error) {
      console.error('Error loading teams:', error);
      showMessage('Erreur lors du chargement des équipes', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showMessage = (message: string, severity: 'success' | 'error') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCreateTeam = async () => {
    if (!name || !shortName || !tla) {
      showMessage('Veuillez remplir les champs obligatoires', 'error');
      return;
    }

    if (tla.length < 2 || tla.length > 3) {
      showMessage('Le TLA doit contenir 2 ou 3 caractères', 'error');
      return;
    }

    try {
      await footballApi.createTeam({
        name,
        shortName,
        tla: tla.toUpperCase(),
        crest: crest || undefined,
      });
      showMessage('Équipe créée avec succès', 'success');
      setDialogOpen(false);
      resetForm();
      loadTeams();
    } catch (error) {
      console.error('Error creating team:', error);
      showMessage('Erreur lors de la création de l\'équipe', 'error');
    }
  };

  const handleDeleteTeam = async (teamId: number) => {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cette équipe ?')) {
      return;
    }

    try {
      await footballApi.deleteTeam(teamId);
      showMessage('Équipe supprimée avec succès', 'success');
      loadTeams();
    } catch (error) {
      console.error('Error deleting team:', error);
      showMessage('Erreur lors de la suppression. L\'équipe est peut-être utilisée dans un match.', 'error');
    }
  };

  const resetForm = () => {
    setName('');
    setShortName('');
    setTla('');
    setCrest('');
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
        <Typography variant="h4">Gestion des Équipes</Typography>
        <Box>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={loadTeams}
            sx={{ mr: 2 }}
          >
            Actualiser
          </Button>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setDialogOpen(true)}
          >
            Nouvelle Équipe
          </Button>
        </Box>
      </Box>

      {teams.length === 0 ? (
        <Alert severity="info">
          Aucune équipe disponible. Créez votre première équipe !
        </Alert>
      ) : (
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={1} mb={2}>
                  <GroupsIcon />
                  <Typography variant="h6">
                    {teams.length} équipe{teams.length > 1 ? 's' : ''} enregistrée{teams.length > 1 ? 's' : ''}
                  </Typography>
                </Box>
                <TableContainer component={Paper} variant="outlined">
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell>Logo</TableCell>
                        <TableCell>Nom</TableCell>
                        <TableCell>Nom Court</TableCell>
                        <TableCell>TLA</TableCell>
                        <TableCell align="center">Actions</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {teams.map((team) => (
                        <TableRow key={team.id}>
                          <TableCell>{team.id}</TableCell>
                          <TableCell>
                            {team.crest ? (
                              <Avatar src={team.crest} alt={team.name} sx={{ width: 32, height: 32 }} />
                            ) : (
                              <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main' }}>
                                {team.tla.charAt(0)}
                              </Avatar>
                            )}
                          </TableCell>
                          <TableCell>
                            <Typography fontWeight="medium">{team.name}</Typography>
                          </TableCell>
                          <TableCell>{team.shortName}</TableCell>
                          <TableCell>
                            <Typography
                              variant="body2"
                              sx={{
                                bgcolor: 'action.selected',
                                px: 1,
                                py: 0.5,
                                borderRadius: 1,
                                display: 'inline-block',
                                fontFamily: 'monospace',
                              }}
                            >
                              {team.tla}
                            </Typography>
                          </TableCell>
                          <TableCell align="center">
                            <IconButton
                              color="error"
                              onClick={() => handleDeleteTeam(team.id)}
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
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Create Team Dialog */}
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Créer une nouvelle équipe</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nom de l'équipe *"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Ex: Paris Saint-Germain"
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Nom court *"
                value={shortName}
                onChange={(e) => setShortName(e.target.value)}
                placeholder="Ex: PSG"
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="TLA (2-3 lettres) *"
                value={tla}
                onChange={(e) => setTla(e.target.value.toUpperCase().slice(0, 3))}
                placeholder="Ex: PSG"
                inputProps={{ maxLength: 3 }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="URL du logo (optionnel)"
                value={crest}
                onChange={(e) => setCrest(e.target.value)}
                placeholder="https://example.com/logo.png"
                helperText="Lien vers une image PNG ou SVG du logo de l'équipe"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Annuler</Button>
          <Button variant="contained" onClick={handleCreateTeam}>
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
