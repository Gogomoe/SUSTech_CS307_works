SELECT (CASE
            WHEN country IN ('kr', 'hk') THEN coalesce(p.surname, ' ') || ' ' || p.first_name
            ELSE coalesce(p.first_name, ' ') || ' ' || p.surname END) AS director
FROM credits
         JOIN people p ON credits.peopleid = p.peopleid AND credited_as = 'D'
         JOIN movies m ON credits.movieid = m.movieid AND year_released = 2016 AND country IN ('kr', 'hk', 'gb', 'ph')
ORDER BY director;