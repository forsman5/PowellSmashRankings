DROP PROCEDURE IF EXISTS InsertTournament;
DELIMITER //
CREATE PROCEDURE InsertTournament
(
IN t_id int,
IN tourney_date Date,
IN tourney_name varchar(255),
IN tourney_link varchar(255)
)
BEGIN
INSERT INTO tournaments (tourney_id,
						  date_started,
						  name,
						  link)
				    VALUES
						(t_id,
						 tourney_date,
						 tourney_name,
						 tourney_link);
END //
DELIMITER ;
