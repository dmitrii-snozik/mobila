import time

def is_valid_int_string(s):
    s = s.strip()
    if s.startswith(('-', '+')):
        return s[1:].isdigit()
    return s.isdigit()

def read_int_from_file(file_path, def_value = 30):
    f = open(file_path, 'r')
    content = f.read().strip()
    f.close()
    if is_valid_int_string(content):
        return int(content)
    else:
        print(f"Неверное значение в {file_path}: '{content}'. Используется значение по умолчанию: {def_value}")
        return def_value
        
while True:
    flag = False
    
    desired_humidity = read_int_from_file('desiredHumidity.txt')
    current_humidity = read_int_from_file('currentHumidity.txt')
    
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
