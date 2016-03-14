FUNCTION NUMERIC GETDISCRIMINANT(NUMERIC A, NUMERIC B, NUMERIC C)
	NUMERIC N;
	N = B * B - 4 * A * C;
	
	IF (N < 0) 
		THEN 
			RETURN 0;
		ELSE 
			IF (N == 0) 
				THEN
					RETURN 1;
				ELSE 
					RETURN 2;
			ENDIF
	ENDIF
END

FUNCTION BOOLEAN MAIN()
	NUMERIC D;
	D = GETDISCRIMINANT(1, -2, -4);
	
	IF (D == 0) 
		THEN 
			PRINT "no roots";
		ELSE
			IF (D == 1)
				THEN
					PRINT "unique root present";
				ELSE 
					PRINT "Two roots available";
			ENDIF
	ENDIF
END 