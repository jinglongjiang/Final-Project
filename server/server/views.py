from django.http import HttpResponse
from django.shortcuts import render
from django.views.decorators import csrf
 
def homepage(request):
    return render(request,'homepage.html')

def upload(request):
    file = request.FILES.get('file')
    with open(file.name,'wb') as f:
        for line in file:
            f.write(line)
    return HttpResponse('succed')