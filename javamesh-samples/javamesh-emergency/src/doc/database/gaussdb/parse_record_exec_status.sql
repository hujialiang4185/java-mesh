DROP FUNCTION
    IF
    EXISTS "public"."parse_record_exec_status";
CREATE FUNCTION "public"."parse_record_exec_status"(recordId INT) RETURNS VARCHAR AS $$
DECLARE
sum_count INT;
wait_count
INT;
process_count
INT;
finish_count
INT;
error_count
INT;
BEGIN
SELECT COUNT
           (1),
       SUM(CASE WHEN status = '0' THEN 1 ELSE 0 END),
       SUM(CASE WHEN status = '1' THEN 1 ELSE 0 END),
       SUM(CASE WHEN status IN ('2', '5') THEN 1 ELSE 0 END),
       SUM(CASE WHEN status IN ('3', '4', '6') THEN 1 ELSE 0 END)
INTO sum_count,
    wait_count,
    process_count,
    finish_count,
    error_count
FROM emergency_exec_record_detail
WHERE record_id = recordId
  AND is_valid = '1';
IF
error_count > 0 THEN
			RETURN '3';

END IF;
	IF
process_count > 0 THEN
			RETURN '1';

END IF;
	IF
finish_count = sum_count THEN
			RETURN '2';

END IF;
	IF
wait_count > 0 THEN
			RETURN '0';

END IF;
	IF
sum_count = 0 THEN
			RETURN '2';

END IF;
RETURN '';

END $$
LANGUAGE plpgsql