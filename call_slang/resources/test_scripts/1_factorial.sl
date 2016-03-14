FUNCTION NUMERIC fact(NUMERIC d)
	IF (d <= 0)
		THEN
			RETURN 1;
		ELSE
			RETURN d * fact(d - 1);
	ENDIF
END

FUNCTION BOOLEAN main()
	NUMERIC d;
	d = 0;
	WHILE (d <= 10)
		PRINTLINE fact(d);
		d = d + 1;
	WEND
END 