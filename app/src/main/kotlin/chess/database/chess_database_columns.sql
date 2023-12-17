-- add columns to table
CREATE TABLE CHESS_DATA (
                            ROUND INT,
                            BOARD_REPRESENTATION VARCHAR(64),
                            PIECE_COUNT JSON, -- Assuming a JSON data type, adjust based on your database
                            LEGAL_MOVES JSON, -- Assuming a JSON data type, adjust based on your database
                            THREATS_AND_ATTACKS JSON, -- Assuming a JSON data type, adjust based on your database
                            PIECE_ACTIVITY JSON, -- Assuming a JSON data type, adjust based on your database
                            KING_SAFETY JSON, -- Assuming a JSON data type, adjust based on your database
                            PAWN_STRUCTURE JSON, -- Assuming a JSON data type, adjust based on your database
                            MATERIAL_BALANCE JSON, -- Assuming a JSON data type, adjust based on your database
                            CENTER_CONTROL JSON, -- Assuming a JSON data type, adjust based on your database
                            PREVIOUS_MOVES JSON -- Assuming a JSON data type, adjust based on your database
);

DROP TABLE CHESS_DATA;

