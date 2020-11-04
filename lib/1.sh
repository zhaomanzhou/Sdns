rm -rf scratch;
mkdir scratch;
cd scratch;
unzip ../P3Full.zip >& /dev/null;
echo "********* Compiling *******" 2>&1 > out;
javac -Xlint:all sdns/serialization/*.java 2>&1 >> out;
cp ../../solution/p3full.jar . >& /dev/null;
cp ../../solution/junit5.jar . >& /dev/null;

echo "********* Tests *******" 2>&1 >> out
java -Dfile.encoding=Unicode -jar junit5.jar --reports-dir report --details-theme ascii --details tree --class-path "p3full.jar:." --select-class sdns.serialization.donatest.ATest --select-class sdns.serialization.donatest.AAAATest --select-class sdns.serialization.donatest.CNameTest --select-class sdns.serialization.donatest.MXTest  --select-class sdns.serialization.donatest.NSTest  --select-class sdns.serialization.donatest.QueryTest --select-class sdns.serialization.donatest.RCodeTest  --select-class sdns.serialization.donatest.ResponseTest  --select-class sdns.serialization.donatest.UnknownTest  --select-class sdns.serialization.donatest.ValidationExceptionTest 2>&1 >> out

sed 's/</\&lt;/g' out > outt;
sed 's/</\&gt;/g' outt > out;
cat out;
