const express = require('express');
const router = express.Router();
const axios = require('axios');

const genreMap = {
  'pop': 132,
  'rock': 152,
  'hip-hop': 116,
  'r&b': 165,
  'rap': 116,
  'indie': 85,
  'country': 84,
  'latin': 197,
  'dance': 106,
  'electronic': 106,
  'christian': 173,
  'gospel': 173
};

router.get('/randomTrack', async (req, res) => {
  const genreName = decodeURIComponent(req.query.genre || 'pop');

  const genreId = genreMap[genreName.toLowerCase()];

  if (!genreId) {
    return res.status(400).json({ error: 'Invalid genre' });
  }

  try {
    const response = await axios.get(`https://api.deezer.com/genre/${genreId}/artists`);
    console.log(response.data);  // Log the response data for debugging
    const artists = response.data.data;

    if (!artists.length) return res.status(404).json({ error: 'No artists found' });

    const randomArtist = artists[Math.floor(Math.random() * artists.length)];

    const topTracksResponse = await axios.get(`https://api.deezer.com/artist/${randomArtist.id}/top?limit=50`);
    const previewableTracks = topTracksResponse.data.data.filter(track => track.preview);

    if (!previewableTracks.length) return res.status(404).json({ error: 'No previewable tracks found' });

    const randomTrack = previewableTracks[Math.floor(Math.random() * previewableTracks.length)];

    res.json({
      trackName: randomTrack.title,
      artist: randomTrack.artist.name,
      preview_url: randomTrack.preview,
      albumArt: randomTrack.album.cover_medium
    });
  } catch (error) {
    console.error("Error fetching track:", error.message);
    res.status(500).json({ error: 'Failed to fetch track' });
  }
});


module.exports = router;
