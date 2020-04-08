SELECT line_id, station_id, number, rank() OVER (PARTITION BY line_id ORDER BY number DESC )
FROM (
         SELECT line_id, l.station_id, number
         FROM line_detail l
                  JOIN stations s ON l.station_id = s.station_id
                  JOIN
              (
                  SELECT station_id, COUNT(*) AS number
                  FROM bus_lines
                  GROUP BY station_id
              ) AS bl ON s.station_id = bl.station_id
         WHERE number >= 10
     ) AS line_bus_count
ORDER BY line_id, number DESC, station_id DESC OFFSET 15
LIMIT 10;
