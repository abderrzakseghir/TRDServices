import { Typography, Alert } from '@mui/material';

export default function Teams() {
  return (
    <div>
      <Typography variant="h4" gutterBottom>
        Gestion des Équipes
      </Typography>
      <Alert severity="info" sx={{ mt: 2 }}>
        Page en construction. Les équipes sont pré-chargées dans la base de données.
      </Alert>
    </div>
  );
}
