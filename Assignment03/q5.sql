WITH count_table AS (
    SELECT district, chr, count((district, chr)) AS cnt
    FROM (SELECT district, substring(chinese_name, 1, 1) AS chr
          FROM stations
          WHERE district IS NOT NULL
            AND district != ''
         ) dist_stat_first_char
    GROUP BY (district, chr))
SELECT c1.district, c1.chr, c1.cnt
FROM count_table c1
         JOIN (SELECT district, MAX(cnt) AS max_cnt
               FROM count_table
               GROUP BY district) c2
              ON c1.district = c2.district AND c1.cnt = c2.max_cnt
ORDER BY district, chr;
