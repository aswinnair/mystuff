label FEDORA VARIANTS
auto 0

	Kernel=$headers.osProfile$/pxe/vmlinuz
	initrd=$headers.osProfile$/pxe/initrd ip="$headers.ip$" host="$headers.hostName$" ks="$headers.CamelHttpUrl$?template=kicstart&ospId=$headers.ospId$"
