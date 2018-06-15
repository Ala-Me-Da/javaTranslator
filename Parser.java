/* *** This file is given as part of the programming assignment. *** */

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    //private int error_sig = 0; //error signal assigned by parsing methods 
   			  //if an error occurs  

    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	scan();
	program();
	if( tok.kind != TK.EOF ) 
	    parse_error("junk after logical end of program"); 
	
    }

    private void program() {
	block();
    }

    private void block(){ 
	declaration_list();
	statement_list();
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that. 
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    private void declaration() {
	mustbe(TK.DECLARE);
	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
	    scan();
	    mustbe(TK.ID);
	}
    }

    private void statement_list() { 
		//maybe move error_sig to be 
		//a local variable here 
		//that way each called block()
		//can assign -1 to error_sig
		//without affecting the block() 
		//that called the new block() 
		//maybe 

	   if(  errorCheck() ) 
		   return; 

	    do { 
		statement(); 
		//System.out.println(tok); 
		//System.out.println(errorCheck()); 
	    } while( !is(TK.EOF) && !errorCheck() ); 
	    //while( !is(TK.EOF) && error_sig != -1 ); 


    }

    private boolean errorCheck() { 
	    boolean error = !( is(TK.ID) || is(TK.PRINT) || is(TK.IF) || 
			       is(TK.DO) || is(TK.TILDE)  );
	    return error;   
    } 
	
    private void statement() { 
	
		if( is(TK.ID) || is(TK.TILDE)  ) assignment(); 
		else if( is(TK.PRINT) ) print(); 
		else if( is(TK.DO) ) mydo(); 
		else if( is(TK.IF) ) myif(); 
		//else if( errorCheck() ) error_sig = -1; 	

    } 

    private void assignment() {  
		ref_id();
	        //System.out.println("Are we in assignment, i=i+1?"); 
		//System.out.println(tok); 	
		mustbe(TK.ASSIGN);
		expr(); 
			        			
    } 

    private void ref_id() { 
		 
		if( is(TK.TILDE) ) { 
			mustbe(TK.TILDE);
		        if( is(TK.NUM) ) 
				mustbe(TK.NUM); 
		}

		mustbe(TK.ID); 		
    } 

    private void print() { 
	mustbe(TK.PRINT);
        //System.out.println("Are we in print()?");
        //System.out.println(tok); 	
	expr(); 	
    } 

    private void mydo() { 
	mustbe(TK.DO);
        //System.out.println("Are we in mydo()?"); 
        //System.out.println(tok); 	
	guarded_command();
	mustbe(TK.ENDDO);
        //System.out.println("Does the loop end?"); 
	//System.out.println(tok);  	
    } 

    private void myif() { 
	//System.out.println("in myif()"); 
	mustbe(TK.IF); 
	guarded_command(); 
	while( is(TK.ELSEIF) ) { 
		//System.out.println("check if in TK.ELSEIF loop");
		mustbe(TK.ELSEIF);  
		guarded_command(); 
	}

	if( is(TK.ELSE) ) { 
		//System.out.println("check if in TK.ELSE");
		mustbe(TK.ELSE);  
		block(); 
	} 
 
		mustbe(TK.ENDIF); 

    } 

    private void guarded_command() {
	   	//System.out.println("in guarded_command()"); 
		expr(); 
		mustbe(TK.THEN); 
		block(); 
		//error_sig = 0; //need this if err_sig gets changed in block
			       //might cause problems for error detection in
			       //this block though...
    } 
    
    private void expr() { 
	        //System.out.println("in expr() "); 
		term();
		while( is(TK.PLUS) || is(TK.MINUS) ) {
		       // System.out.println("check if expr() loop executes");
		       // System.out.println("are we eval-ing '+1'?"); 
			//System.out.println(tok); 	
			if( is(TK.PLUS) ) mustbe(TK.PLUS); 
			else mustbe(TK.MINUS); 
			term(); 
		} 

    } 

    private void term() { 
	    	//System.out.println("in term() "); 
		factor(); 
		while( is(TK.TIMES) || is(TK.DIVIDE) ) { 
			if( is(TK.TIMES) ) mustbe(TK.TIMES); 
			else mustbe(TK.DIVIDE); 
			factor(); 
		} 
    } 

    private void factor() { 
	        //System.out.println("in factor() "); 
		if( is(TK.LPAREN) ) { 
			mustbe(TK.LPAREN);
			expr(); 
			mustbe(TK.RPAREN); 	
		} else if ( is(TK.NUM) ) {  
			//System.out.println("check if TK.NUM case in factor() "); 
			mustbe(TK.NUM); 
		} else {
			//System.out.println("Do we go in factor()'s ref_id()?"); 
			//System.out.println(tok); 
			ref_id(); 
		} 
    } 

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    } 


    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	}
	scan();
    }

    private void parse_error(String msg) {
	System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
       // System.out.println(tok); 	
	System.exit(1);
    }
}
