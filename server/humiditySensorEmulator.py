import time

while True:
    flag = False
    f = open('desiredHumidity.txt', 'r')
    desired_humidity = int(f.read())
    f.close()
    f = open('currentHumidity.txt', 'r')
    current_humidity = int(f.read())
    f.close()
    if(desired_humidity > current_humidity and current_humidity < 100):
        current_humidity+=1
        flag = True
    elif(desired_humidity < current_humidity and current_humidity > 0):
        current_humidity-=1
        flag = True
    if(flag):
        f = open('currentHumidity.txt', 'w')
        f.write(str(current_humidity))
        f.close()
    time.sleep(5)