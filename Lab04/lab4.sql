SELECT year_released, COUNT(*) AS COUNT
FROM movies
WHERE country = 'cn'
GROUP BY year_released;
