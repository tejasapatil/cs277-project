start_time=`date +%s`
rm $2
sort -n $1 > $1.sort
exec 3<$1.sort
exec 4<review.index.1
while IFS= read -r line1 <&3
      IFS= read -r line2 <&4
do  
        echo $line2 `echo $line1 | awk '{ print $2}'` >> $2
done
rm $1.sort
end_time=`date +%s`
echo execution time was `expr $end_time - $start_time` s.
