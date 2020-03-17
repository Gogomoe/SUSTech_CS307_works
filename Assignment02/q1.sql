SELECT title, country, year_released
FROM movies
WHERE country != 'us'
  AND year_released = 1991
  AND upper(title) LIKE 'THE%';