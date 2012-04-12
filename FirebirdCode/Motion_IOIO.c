/*
	Developed by : Hemant Gangolia
				   Chinmay Chauhan
				   Sagar Chordia
				   Saif Hasan

	This code has been developed as a part of ERTS project at IIT Bombay.
	It provides an interface between Firebird Atmega 2560 and the Bluetooth 
	Module attached on it.
*/


#include<avr/io.h>
#include<avr/interrupt.h>
#include<util/delay.h>

#include <math.h> //included to support power function

#include "lcd.c"


#define FCPU 11059200ul //defined here to make sure that program works properly

unsigned char data; //to store received data from UDR1

unsigned char tx_data; //to store data to be transmitted to UDR1

void port_init();
void timer5_init();
void velocity(unsigned char, unsigned char);
void motors_delay();

unsigned char ADC_Conversion(unsigned char);
unsigned char ADC_Value;
unsigned char flag1 = 0;
unsigned char flag2 = 0;
unsigned char Left_white_line = 0;
unsigned char Center_white_line = 0;
unsigned char Right_white_line = 0;
unsigned char Front_Sharp_Sensor=0;
unsigned char Front_IR_Sensor=0;

unsigned char motion_sensor_flag;

unsigned char sent_data;

//Function to configure LCD port
void lcd_port_config (void)
{
 DDRC = DDRC | 0xF7; //all the LCD pin's direction set as output
 PORTC = PORTC & 0x80; // all the LCD pins are set to logic 0 except PORTC 7
}

//ADC pin configuration
void adc_pin_config (void)
{
 DDRF = 0x00; 
 PORTF = 0x00;
 DDRK = 0x00;
 PORTK = 0x00;
}

//Function to configure ports to enable robot's motion
void motion_pin_config (void) 
{
 DDRA = DDRA | 0x0F;
 PORTA = PORTA & 0xF0;
 DDRL = DDRL | 0x18;   //Setting PL3 and PL4 pins as output for PWM generation
 PORTL = PORTL | 0x18; //PL3 and PL4 pins are for velocity control using PWM.
}

//Function to initialize Buzzer 
void buzzer_pin_config (void)
{
 DDRC = DDRC | 0x08;		//Setting PORTC 3 as outpt
 PORTC = PORTC & 0xF7;		//Setting PORTC 3 logic low to turnoff buzzer
}


// Timer 5 initialised in PWM mode for velocity control
// Prescale:64
// PWM 8bit fast, TOP=0x00FF
// Timer Frequency:674.988Hz
void timer5_init()
{
	TCCR5B = 0x00;	//Stop
	TCNT5H = 0xFF;	//Counter higher 8-bit value to which OCR5xH value is compared with
	TCNT5L = 0x01;	//Counter lower 8-bit value to which OCR5xH value is compared with
	OCR5AH = 0x00;	//Output compare register high value for Left Motor
	OCR5AL = 0xFF;	//Output compare register low value for Left Motor
	OCR5BH = 0x00;	//Output compare register high value for Right Motor
	OCR5BL = 0xFF;	//Output compare register low value for Right Motor
	OCR5CH = 0x00;	//Output compare register high value for Motor C1
	OCR5CL = 0xFF;	//Output compare register low value for Motor C1
	TCCR5A = 0xA9;	///{COM5A1=1, COM5A0=0; COM5B1=1, COM5B0=0; COM5C1=1 COM5C0=0}
 					  //For Overriding normal port functionalit to OCRnA outputs.
				  	  //{WGM51=0, WGM50=1} Along With WGM52 in TCCR5B for Selecting FAST PWM 8-bit Mode
	TCCR5B = 0x0B;	//WGM12=1; CS12=0, CS11=1, CS10=1 (Prescaler=64)
}


void adc_init()
{
	ADCSRA = 0x00;
	ADCSRB = 0x00;		//MUX5 = 0
	ADMUX = 0x20;		//Vref=5V external --- ADLAR=1 --- MUX4:0 = 0000
	ACSR = 0x80;
	ADCSRA = 0x86;		//ADEN=1 --- ADIE=1 --- ADPS2:0 = 1 1 0
}

//Function For ADC Conversion
unsigned char ADC_Conversion(unsigned char Ch) 
{
	unsigned char a;
	if(Ch>7)
	{
		ADCSRB = 0x08;
	}
	Ch = Ch & 0x07;  			
	ADMUX= 0x20| Ch;	   		
	ADCSRA = ADCSRA | 0x40;		//Set start conversion bit
	while((ADCSRA&0x10)==0);	//Wait for conversion to complete
	a=ADCH;
	ADCSRA = ADCSRA|0x10; //clear ADIF (ADC Interrupt Flag) by writing 1 to it
	ADCSRB = 0x00;
	return a;
}

//Function To Print Sesor Values At Desired Row And Coloumn Location on LCD
void print_sensor(char row, char coloumn,unsigned char channel)
{
	
	ADC_Value = ADC_Conversion(channel);
	lcd_print(row, coloumn, ADC_Value, 3);
}

//Function for velocity control
void velocity (unsigned char left_motor, unsigned char right_motor)
{
	OCR5AL = (unsigned char)left_motor;
	OCR5BL = (unsigned char)right_motor;
}

//Function used for setting motor's direction
void motion_set (unsigned char Direction)
{
 unsigned char PortARestore = 0;

 Direction &= 0x0F; 		// removing upper nibbel for the protection
 PortARestore = PORTA; 		// reading the PORTA original status
 PortARestore &= 0xF0; 		// making lower direction nibbel to 0
 PortARestore |= Direction; // adding lower nibbel for forward command and restoring the PORTA status
 PORTA = PortARestore; 		// executing the command
}


void forward (void) //both wheels forward
{
  motion_set(0x06);
}

void back (void) //both wheels backward
{
  motion_set(0x09);
}

void left (void) //Left wheel backward, Right wheel forward
{
  motion_set(0x05);
}

void right (void) //Left wheel forward, Right wheel backward
{
  motion_set(0x0A);
}

void soft_left (void) //Left wheel stationary, Right wheel forward
{
 motion_set(0x04);
}

void soft_right (void) //Left wheel forward, Right wheel is stationary
{
 motion_set(0x02);
}

void soft_left_2 (void) //Left wheel backward, right wheel stationary
{
 motion_set(0x01);
}

void soft_right_2 (void) //Left wheel stationary, Right wheel backward
{
 motion_set(0x08);
}

void stop (void) //hard stop
{
  motion_set(0x00);
}


//Function to initialize ports
void port_init()
{
	lcd_port_config();
	adc_pin_config();
   motion_pin_config();
   buzzer_pin_config();
}

void buzzer_on (void)
{
unsigned char port_restore = 0;
port_restore = PINC;
port_restore = port_restore | 0x08;
PORTC = port_restore;
}

void buzzer_off (void)
{
unsigned char port_restore = 0;
port_restore = PINC;
port_restore = port_restore & 0xF7;
PORTC = port_restore;
}

//Function To Initialize all The Devices
void init_devices()
{
cli(); //Clears the global interrupts
port_init();  //Initializes all the ports
adc_init();
timer5_init();
sei();   //Enables the global interrupts
}




// Code for BlueTooth

unsigned char main_buf[100],maincnt; 
unsigned char ser_buf[100],ser_trax_buf[100];
unsigned int count,trx_count,trx_curr_cnt;
unsigned char decode,res_wait;
unsigned char data;


void Init_USART3()
{
       UCSR3B = 0x00;
       UCSR3A = 0x00;
       UCSR3C  = 0x06;
       UBRR3L = 0x47; //5F
       UBRR3H = 0x00;
       UCSR3B = 0xd8;
}

/*
	Recieve Interrupt Handler
	Returns Character String Arrived in main_buf array
	Indicates Main Routine after Whole String Has Arrived
*/
SIGNAL(SIG_USART3_RECV)
{			
       data = UDR3;	   
}


//Main Function
int main(void)
{
	
   	init_devices();
   	Init_USART3();
	lcd_set_4bit();
	lcd_init();

	int flag_buzzer_on = 0; //  initially buzzer is off	

	unsigned int signal;

	velocity(120,120);

	while(1) {
		
		signal = (unsigned int)data;
		
		switch(signal) {
			case 8:
				forward();			
				break;
			
			case 2:
				back();
				break;

			case 4:
				soft_left();
				break;
			
			case 6: 
				soft_right();
				break;
			
			case 7:
				if (flag_buzzer_on == 0){
					buzzer_on();
					flag_buzzer_on = 1;
				} 
				else if (flag_buzzer_on == 1) {
					buzzer_off();
					flag_buzzer_on = 0;
				}
				break;
			
			// If none of the above matches then stop
			default: 
				lcd_print(1, 1, 0, 1);				
				stop();
				break;				

		}
		_delay_ms(100);
		lcd_cursor(2,1);
		char buffer[2];
		buffer[0] = '0' + (data & 0xff);
		buffer[1] = '\0';
		lcd_string(buffer);
		
	}

	return 0;
}
