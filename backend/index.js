const express = require('express');
const app = express();
const cors = require('cors');
const deezerRoutes = require('./routes/deezer'); // ✅ your deezer routes
const leaderboardRoutes = require('./routes/leaderboard');  // ✅ your leaderboard routes

app.use(cors());
app.use(express.json());

app.get('/', (req, res) => {
  res.send('Hello, World! Your app is running!');
});

// Proxy Deezer API requests to '/deezer'
app.use('/deezer', deezerRoutes);

// Proxy Leaderboard API requests to '/api'
app.use('/api', leaderboardRoutes);
const port = process.env.PORT || 3000;
// Start server on a single port
app.listen(port, () => {
  console.log('Server running on port 3000');
});