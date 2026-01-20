import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  Button,
  CircularProgress,
} from '@mui/material';
import {
  SportsScore,
  Schedule,
  PlayCircle,
  CheckCircle,
} from '@mui/icons-material';
import { footballApi } from '../services/api';

export default function Dashboard() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    total: 0,
    scheduled: 0,
    inPlay: 0,
    finished: 0,
  });

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const data = await footballApi.getMatches();
      const matches = data.matches;
      
      setStats({
        total: matches.length,
        scheduled: matches.filter(m => m.status === 'SCHEDULED').length,
        inPlay: matches.filter(m => m.status === 'IN_PLAY').length,
        finished: matches.filter(m => m.status === 'FINISHED').length,
      });
    } catch (error) {
      console.error('Error loading stats:', error);
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, icon, color }: any) => (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Box>
            <Typography color="textSecondary" gutterBottom>
              {title}
            </Typography>
            <Typography variant="h3">
              {loading ? <CircularProgress size={40} /> : value}
            </Typography>
          </Box>
          <Box
            sx={{
              backgroundColor: color,
              borderRadius: '50%',
              p: 2,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>
      <Typography variant="body1" color="textSecondary" sx={{ mb: 4 }}>
        Bienvenue sur le simulateur d'API Football
      </Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Matchs Total"
            value={stats.total}
            icon={<SportsScore sx={{ fontSize: 40, color: 'white' }} />}
            color="primary.main"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="À Venir"
            value={stats.scheduled}
            icon={<Schedule sx={{ fontSize: 40, color: 'white' }} />}
            color="info.main"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="En Cours"
            value={stats.inPlay}
            icon={<PlayCircle sx={{ fontSize: 40, color: 'white' }} />}
            color="success.main"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Terminés"
            value={stats.finished}
            icon={<CheckCircle sx={{ fontSize: 40, color: 'white' }} />}
            color="default"
          />
        </Grid>
      </Grid>

      <Card>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Actions Rapides
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
            <Button
              variant="contained"
              color="primary"
              onClick={() => navigate('/matches')}
              startIcon={<SportsScore />}
            >
              Créer un Match
            </Button>
            <Button
              variant="contained"
              color="secondary"
              onClick={() => navigate('/simulation')}
              startIcon={<PlayCircle />}
            >
              Lancer Simulation
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}
