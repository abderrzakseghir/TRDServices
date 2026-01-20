import { Typography, Alert } from '@mui/material';

export default function Matches() {
  return (
    <div>
      <Typography variant="h4" gutterBottom>
        Gestion des Matchs
      </Typography>
      <Alert severity="info" sx={{ mt: 2 }}>
        Page en construction. Utilisez la page Simulation pour voir les matchs existants.
      </Alert>
    </div>
  );
}
