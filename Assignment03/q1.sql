SELECT station_id AS a_station
FROM line_detail
WHERE line_id = 1
  AND station_id NOT IN
      (
          SELECT station_id AS b_station
          FROM line_detail
          WHERE line_id = 2
      )
ORDER BY a_station;