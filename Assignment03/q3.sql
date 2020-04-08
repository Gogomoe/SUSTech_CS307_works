SELECT district,
       number,
       RANK() OVER (ORDER BY number DESC ) AS rank
FROM (
         SELECT district, COUNT(DISTINCT line_id) AS number
         FROM line_detail l
                  JOIN stations s ON l.station_id = s.station_id
         WHERE district IS NOT NULL
           AND district != ''
         GROUP BY district
     ) AS dist_line_count
ORDER BY number DESC, district DESC;