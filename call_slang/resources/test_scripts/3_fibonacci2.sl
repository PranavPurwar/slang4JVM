FUNCTION NUMERIC fibonacci(NUMERIC n)
	IF (n == 0)
		THEN
			RETURN 0;
		ELSE
			IF (n == 1)
				THEN 
					RETURN 1;
				ELSE 
					RETURN fibonacci(n-1) + fibonacci(n-2);
			ENDIF
	ENDIF
END

FUNCTION BOOLEAN main()
	NUMERIC d;
	d = 0;
	WHILE (d <= 10)
		PRINTLINE fibonacci(d);
		d = d + 1;
	WEND
END
		