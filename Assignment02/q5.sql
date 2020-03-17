WITH films_actor AS (
    SELECT m.movieid AS movieid, count(*) cnt
    FROM movies m
             JOIN credits c ON m.movieid = c.movieid AND m.year_released >= 2000
             JOIN people p on c.peopleid = p.peopleid AND c.credited_as = 'A' AND p.born >= 2000
    GROUP BY m.movieid
)
SELECT *
FROM films_actor
WHERE cnt = (SELECT max(cnt) FROM films_actor);