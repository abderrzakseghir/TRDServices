import { Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Matches from './pages/Matches';
import Simulation from './pages/Simulation';
import Teams from './pages/Teams';

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/matches" element={<Matches />} />
        <Route path="/simulation" element={<Simulation />} />
        <Route path="/teams" element={<Teams />} />
      </Routes>
    </Layout>
  );
}

export default App;
