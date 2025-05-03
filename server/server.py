from flask import Flask, jsonify
app = Flask(__name__)
 
@app.route('/sendDesiredHumidity/<int:humidity>', methods=['POST'])
def send_desired_humidity(humidity):
    f = open('desiredHumidity.txt', 'w')
    f.write(str(humidity))
    f.close()
    print(f'new desired humidity {humidity}')
    return jsonify({'result': True})
 
@app.route('/setCurrentHumidity/<int:humidity>', methods=['POST'])
def set_current_humidity(humidity):
    f = open('currentHumidity.txt', 'w')
    f.write(str(humidity))
    f.close()
    print(f'new current humidity {humidity}')
    return jsonify({'result': True})
    
@app.route('/getCurrentHumidity/', methods=['GET'])
def get_current_humidity():
    f = open('currentHumidity.txt', 'r')
    current_humidity = f.read()
    f.close()
    print(f'current humidity was send {current_humidity}')
    return jsonify({'humidity': current_humidity})
 
@app.route('/getDesiredHumidity/', methods=['GET'])
def get_desired_humidity():
    f = open('desiredHumidity.txt', 'r')
    desired_humidity = f.read()
    f.close()
    print(f'desired humidity was send {desired_humidity}')
    return jsonify({'humidity': desired_humidity})
 
if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")