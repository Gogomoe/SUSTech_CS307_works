SELECT count(*)
FROM (SELECT count(*) AS movie_count
      FROM credits
      WHERE credited_as = 'A'
      GROUP BY peopleid) AS mc
WHERE mc.movie_count > 30;