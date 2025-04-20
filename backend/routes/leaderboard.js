const express = require('express');
const router = express.Router();
const db = require('../db');  // Assuming you have a database connection in db.js

// POST /api/submitScore
router.post('/submitScore', async (req, res) => {
  const { play_name, genre, score } = req.body;
  try {
    await db.query(
      'INSERT INTO leaderboard (play_name, genre, score) VALUES ($1, $2, $3)',
      [play_name, genre, score]
    );
    res.status(201).json({ message: 'Score submitted' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /api/getLeaderboard
router.get('/getLeaderboard', async (req, res) => {
  try {
    const result = await db.query(
      'SELECT ROW_NUMBER() OVER (ORDER BY score DESC) AS rank, play_name, score, genre FROM leaderboard ORDER BY score DESC LIMIT 10'
    );
    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});


module.exports = router;

