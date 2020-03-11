SELECT year_released, COUNT(*) AS COUNT
FROM movies
WHERE country = 'cn'
  AND year_released >= 1960
GROUP BY year_released;
