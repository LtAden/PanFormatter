Inputs:
- IIN Range to format configuration - input is provided in the CSV file, program has to read it (decision if statically or file path configurable as program param is up to Your preference)
- PAN Number

Outputs:
- Formatted PAN - in case of positive match
- Exception with a message - in case of not supported pan

Goals:
- PAN Number needs to be matched to the IIN Range
- not all of IIN Ranges define the same prefix length
- each IIN Range can have specific formatting for specific PAN length
- program needs to decide on the best match of the record in configuration
- in case of unsupported formatting user has to be notified about that fact
- when match of IIN Range leads to supported formatting, program should return formatted PAN

NFR:
- Code coverage over 70% - aim for 100%
- clean code
- edge cases scenarios covered

examples:
- input: 4444444444444444 
- match: Visa (incl. VPay);16;1;4;4;#### #### #### #### 
- output: 4444 4444 4444 4444


- input: 30122994494222
- match: Diners Club Carte Blanche;14;3;300;305;#### ###### ####
- output: 3012 299449 4222
