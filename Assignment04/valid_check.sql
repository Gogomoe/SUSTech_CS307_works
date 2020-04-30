CREATE OR REPLACE FUNCTION valid_check(id VARCHAR) RETURNS VOID AS
$$
DECLARE
    address VARCHAR;
    year    INT;
    month   INT;
    day     INT;
BEGIN

    IF LENGTH(id) != 18 THEN
        RAISE EXCEPTION 'length error';
    END IF;

    IF NOT (SELECT id ~ '^[0-9]{17}[0-9xX]$') THEN
        RAISE EXCEPTION 'format error';
    END IF;

    address := SUBSTRING(id, 1, 6);

    IF (SELECT COUNT(*) FROM district WHERE code = address) = 0 THEN
        RAISE EXCEPTION 'error address';
    END IF;

    year := substring(id, 7, 4)::INT;
    IF year < 1900 THEN
        RAISE EXCEPTION 'error year';
    END IF;

    month := substring(id, 11, 2)::INT;
    IF month <= 0 OR month > 12 THEN
        RAISE EXCEPTION 'error month';
    END IF;

    day := substring(id, 13, 2)::INT;
    IF day <= 0 OR month > 31 THEN
        RAISE EXCEPTION 'error day';
    END IF;

    PERFORM (SELECT (SUBSTRING(id, 7, 8))::DATE);

    IF (SELECT CASE (12 - t2.S % 11) % 11
                   WHEN 10 THEN 'X'
                   ELSE ((12 - t2.S % 11) % 11)::CHAR
                   END res
        FROM (
                 SELECT SUM(t1.di * t1.wi) S
                 FROM (
                          SELECT SUBSTRING(id, n.i, 1)::Int di, pow(2, 18 - n.i)::INT % 11 wi
                          FROM (SELECT GENERATE_SERIES(1, 17) i) n
                      ) t1
             ) t2
       ) != UPPER(SUBSTRING(id, 18, 1)) THEN
        RAISE EXCEPTION 'error checksum';
    END IF;

END;
$$ LANGUAGE plpgsql;