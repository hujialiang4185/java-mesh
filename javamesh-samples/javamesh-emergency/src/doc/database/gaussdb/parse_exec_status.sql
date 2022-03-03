DROP FUNCTION
    IF
    EXISTS "public"."parse_exec_status";
CREATE FUNCTION "public"."parse_exec_status"(exec_status VARCHAR) RETURNS VARCHAR AS $$
DECLARE
resultStr VARCHAR ( 255 ) DEFAULT '';
BEGIN-- Routine body goes here...
	IF
exec_status = '0' THEN
			resultStr := '待执行';

		ELSEIF
exec_status = '1' THEN
			resultStr := '正在执行';

			ELSEIF
exec_status = '2' THEN
				resultStr := '执行成功';

				ELSEIF
exec_status = '3' THEN
					resultStr := '执行失败';

					ELSEIF
exec_status = '4' THEN
						resultStr := '执行取消';

						ELSEIF
exec_status = '5' THEN
							resultStr := '人工确认成功';

							ELSEIF
exec_status = '6' THEN
								resultStr := '人工确认失败';
ELSE resultStr := exec_status;

END IF;
RETURN resultStr;

END $$
LANGUAGE plpgsql