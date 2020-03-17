SELECT p.*
FROM (
         SELECT cm.peopleid, count(*) AS country_cnt
         FROM (
                  SELECT DISTINCT c.peopleid, m.country
                  FROM credits c
                           JOIN movies m on c.movieid = m.movieid AND c.credited_as = 'D'
              ) cm
         GROUP BY cm.peopleid
         HAVING count(*) > 1
     ) dirs
         JOIN people AS p ON dirs.peopleid = p.peopleid;