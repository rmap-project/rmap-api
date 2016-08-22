#!/bin/ksh
echo "Content Type: text/html\n"
/rmap/noid/noidminter/noid -f /rmap/noid/noiddb mint `echo $QUERY_STRING | sed 's/[ = ]*$//'`
