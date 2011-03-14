<resources xmlns="http://in.thunk.test.camel.http.basic.beans/InstallResource">
	<resource>
		<name>pxeconfig</name>
		<url>$headers.CamelHttpUrl$?ospId=$headers.ospId$&amp;template=pxeconfig-$headers.os$</url>
		<localPath>pxelinux.cfg</localPath>
	</resource>
	<resource>
		<name>kernel</name>
		<url>http://localhost:9090/imgmgnt/static?q=$headers.osProfile$/boot/vmlinuz&amp;ospId=$headers.ospid$</url>
		<localPath>$headers.osProfile$/vmlinuz</localPath>
	</resource>
	<resource>
		<name>initrd</name>
		<url>http://localhost:9090/imgmgnt/static?q=$headers.osProfile$/boot/initrd.gz&amp;ospId=$headers.ospid$</url>
		<localPath>$headers.osProfile$/initrd.gz</localPath>
	</resource>	
</resources>