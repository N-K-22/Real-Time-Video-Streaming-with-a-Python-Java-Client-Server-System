# Real-Time-Video-Streaming-with-a-Python-Java-Client-Server-System
A real-time multimedia communication system involving cross-platform networking


Based on experimentally examining the frame size, the maximum number of bytes for each fram is 2073600. For this reason, the code is specifically designed to handle this alrge number in a specific way. All things considered, the nuanced approach will simply regard the maximum number of bytes to send. For debugging purposes and to ensure accurate execution, this number will be assumed 


https://stackoverflow.com/questions/3396813/message-too-long-for-udp-socket-after-setting-sendbuffersize


For this implementation, the assumption that no segment is lost is assumed for ease. One big thing I noticed after scouring multiple stack exchanges to see if there is some way to bchange the size and sadly there appears not ot be