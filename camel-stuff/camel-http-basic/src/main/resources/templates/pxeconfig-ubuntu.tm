label UBUNTU1010
auto 0

	Kernel=$headers.osProfile$/pxe/vmlinuz
	initrd=$headers.osProfile$/pxe/initrd ip="$headers.ip$" host="$headers.hostName$" preseed="$headers.CamelHttpUrl$?template=preseed&ospId=$headers.ospId$"
