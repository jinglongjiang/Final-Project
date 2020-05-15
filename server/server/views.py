from django.http import HttpResponse
from django.shortcuts import render
from django.views.decorators import csrf
from tensorflow.python.platform import gfile
from django.core.files.uploadedfile import InMemoryUploadedFile
import tensorflow as tf
import numpy as np
import re,os,requests

os.environ["CUDA_VISIBLE_DEVICES"] = '0' #use GPU with ID=0
config = tf.compat.v1.ConfigProto()
config.gpu_options.per_process_gpu_memory_fraction = 0.5 # maximun alloc gpu50% of MEM
config.gpu_options.allow_growth = True #allocate dynamically

class NodeLookup(object):
  def __init__(self,
               label_lookup_path=None,
               uid_lookup_path=None):
    if not label_lookup_path:
      label_lookup_path = 'server/imagenet_2012_challenge_label_map_proto.pbtxt'
    if not uid_lookup_path:
      uid_lookup_path = 'server/imagenet_synset_to_human_label_map.txt'
    self.node_lookup = self.load(label_lookup_path, uid_lookup_path)
 
  def load(self, label_lookup_path, uid_lookup_path):
    if not gfile.Exists(uid_lookup_path):
      tf.logging.fatal('File does not exist %s', uid_lookup_path)
    if not gfile.Exists(label_lookup_path):
      tf.logging.fatal('File does not exist %s', label_lookup_path)
 
    # Loads mapping from string UID to human-readable string
    proto_as_ascii_lines = tf.io.gfile.GFile(uid_lookup_path).readlines()
    uid_to_human = {}
    p = re.compile(r'[n\d]*[ \S,]*')
    for line in proto_as_ascii_lines:
      parsed_items = p.findall(line)
      uid = parsed_items[0]
      human_string = parsed_items[2]
      uid_to_human[uid] = human_string
 
    # Loads mapping from string UID to integer node ID.
    node_id_to_uid = {}
    proto_as_ascii = tf.io.gfile.GFile(label_lookup_path).readlines()
    for line in proto_as_ascii:
      if line.startswith('  target_class:'):
        target_class = int(line.split(': ')[1])
      if line.startswith('  target_class_string:'):
        target_class_string = line.split(': ')[1]
        node_id_to_uid[target_class] = target_class_string[1:-2]
 
    # Loads the final mapping of integer node ID to human-readable string
    node_id_to_name = {}
    for key, val in node_id_to_uid.items():
      if val not in uid_to_human:
        tf.logging.fatal('Failed to locate: %s', val)
      name = uid_to_human[val]
      node_id_to_name[key] = name
 
    return node_id_to_name
 
  def id_to_string(self, node_id):
    if node_id not in self.node_lookup:
      return ''
    return self.node_lookup[node_id]
 
#读取训练好的Inception-v3模型来创建graph
def create_graph():
  with gfile.FastGFile('server/classify_image_graph_def.pb', 'rb') as f:
    graph_def = tf.compat.v1.GraphDef()
    graph_def.ParseFromString(f.read())
    tf.import_graph_def(graph_def, name='')




def admin(request):
    return render(request,'test.html')


def upload(request):
  image = request.FILES.get("image")
  with open("images/a.jpeg",'wb') as f:
    f.write(image.read())
  
  image_data = gfile.FastGFile("images/a.jpeg", 'rb').read()
  create_graph()
  
  sess=tf.compat.v1.Session()
  #Inception-v3模型的最后一层softmax的输出
  softmax_tensor= sess.graph.get_tensor_by_name('softmax:0')
  #输入图像数据，得到softmax概率值（一个shape=(1,1008)的向量）
  predictions = sess.run(softmax_tensor,{'DecodeJpeg/contents:0': image_data})
  #(1,1008)->(1008,)
  predictions = np.squeeze(predictions)
  
  # ID --> English string label.
  node_lookup = NodeLookup()
  #取出前5个概率最大的值（top-5)
  top_5 = predictions.argsort()[-5:][::-1]
  a = []
  for node_id in top_5:
      human_string = node_lookup.id_to_string(node_id)
      a.append(human_string)
      score = predictions[node_id]
      print(human_string,score)
  
  sess.close()
  return HttpResponse(a[0])

def cut(request):
  response = requests.post(
    'https://api.remove.bg/v1.0/removebg',
    files={'image_file': open('images/a.jpeg', 'rb')},
    data={'size': 'auto'},
    headers={'X-Api-Key': '37DewWLXcs7TF8yJR3RrMRQK'},
  )
  if response.status_code == requests.codes.ok:
    with open('images/a.png', 'wb') as out:
        out.write(response.content)
  else:
    return HttpResponse("Error:", response.status_code, response.text)
  with open('images/a.png','rb') as f:
    image_data = f.read()
    return HttpResponse(image_data,content_type="image/png")