const { Client } = require('pg'); // Assuming you're using the 'pg' library

// Use DATABASE_URL from Heroku's environment variables
const client = new Client({
  connectionString: process.env.DATABASE_URL, // Use the Heroku provided DATABASE_URL
  ssl: {
    rejectUnauthorized: false // Required by Heroku
  }
});

client.connect()
  .then(() => console.log('Connected to the database'))
  .catch((err) => console.error('Connection error', err.stack));

module.exports = client;
