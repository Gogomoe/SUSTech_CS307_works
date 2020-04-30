CREATE OR REPLACE FUNCTION people_trigger_fun() RETURNS TRIGGER AS
$$
DECLARE
    province_code VARCHAR = SUBSTRING(NEW.id, 1, 2);
    city_code     VARCHAR = SUBSTRING(NEW.id, 3, 2);
    district_code VARCHAR = SUBSTRING(NEW.id, 5, 2);
    full_code     VARCHAR = SUBSTRING(NEW.id, 1, 6);
    address       VARCHAR = '';
BEGIN
    PERFORM (SELECT valid_check(NEW.id));

    address := (
        SELECT string_agg(x.name, ',')
        FROM (
                 SELECT name
                 FROM district
                 WHERE code in (
                                province_code || '0000',
                                province_code || city_code || '00',
                                full_code
                     )
                 ORDER BY code
             ) x
    );

    NEW.address := address;
    NEW.birthday := SUBSTRING(NEW.id, 7, 8);

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER people_trigger
    BEFORE INSERT
    ON people
    FOR EACH ROW
EXECUTE PROCEDURE people_trigger_fun();