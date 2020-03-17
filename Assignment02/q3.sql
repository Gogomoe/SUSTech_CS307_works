WITH counts AS (SELECT country, count(*) AS count
                FROM movies
                WHERE year_released BETWEEN 1970 AND 1979
                GROUP BY country)
SELECT round(100 * (SELECT count FROM counts WHERE country = 'us') / (SELECT sum(count) FROM counts), 2) AS us_percent;