WITH liuyifei_id AS
         (
             SELECT peopleid
             FROM people
             WHERE surname = 'Liu'
               AND first_name = 'Yifei'
         ),
     movies_played AS
         (
             SELECT DISTINCT movieid
             FROM credits
             WHERE peopleid = (SELECT * FROM liuyifei_id)
         )

SELECT count(DISTINCT peopleid) AS count
FROM movies_played m
         JOIN credits c ON m.movieid = c.movieid AND credited_as = 'A'
WHERE peopleid != (SELECT * FROM liuyifei_id);