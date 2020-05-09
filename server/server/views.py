from django.http import HttpResponse
from django.shortcuts import render
from django.views.decorators import csrf
from PIL import Image
import numpy as np
import flask
from flask import render_template, redirect
from flask import request, url_for, render_template, redirect

def admin(request):

        
    return render(request,'test.html')

def upload(request):
    file = request.FILES.get('file')
    image = image1.save(os.path.join(app.config['UPLOAD_FOLDER'], image1.filename)) 
    with open(os.path.join(app.config['UPLOAD_FOLDER'], image1.filename), 'rb') as f:
    image = Image.open(io.BytesIO(f.read()))        
    processed_image = prepare_image(image, target=(224, 224))

            global sess
            global graph
            with graph.as_default():
                set_session(sess)
                preds = model.predict(processed_image)
                results = imagenet_utils.decode_predictions(preds)
                data["predictions"] = []

            data["success"] = "Uploaded"
            title = "predict"
            
    with open(file.name,'wb') as f:
        for line in file:
            f.write(line)
    a = '345'
    return HttpResponse(a)