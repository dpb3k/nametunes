const { Pool } = require('pg');

// Ensure this is the correct connection string provided by Render
const pool = new Pool({
    connectionString: process.env.DATABASE_URL, // This is the URL that should be set in Render's environment variables
    ssl: {
        rejectUnauthorized: false // This might be necessary when using Render's hosted Postgres
    }
});

pool.query('SELECT NOW()')
    .then(res => console.log(res.rows[0]))
    .catch(err => console.log('Error connecting to database', err));


module.exports = {
    query: (text, params) => pool.query(text, params),
};
