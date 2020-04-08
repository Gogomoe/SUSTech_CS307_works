SELECT district,
       number,
       rank() OVER ( ORDER BY number) AS rank
FROM (
         SELECT district, COUNT(*) AS number
         FROM line_detail
                  JOIN stations s ON line_detail.station_id = s.station_id
         WHERE line_id = 1
         GROUP BY district
     ) AS dist_numbe
ORDER BY number, district;