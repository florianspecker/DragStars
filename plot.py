# -*- coding: utf-8 -*-
"""
Created on Sat Oct 11 00:11:34 2014

@author: ralf
"""

import json
import numpy as np
from matplotlib import pyplot as plt


def get_accel(name):
    config = json.loads(open(name).read())
    acc_x = []
    acc_y = []
    acc_z = []
    #Winkelbeschl.
    w_x = []
    w_y = []
    w_z = []
    t = []
    round_t = []
    t0 = 0.    
    
    for ar in config:
        if (ar['type'] == '0'):
            if (ar['timestamp_sent'] is not None):
                if(t0 == 0.):
                    t0 = float(ar['timestamp_received'])
                t.append(float(ar['timestamp_received']))
            if (ar['acc_x'] is not None):
                acc_x.append(float(ar['acc_x']))
                acc_y.append(float(ar['acc_y']))
                acc_z.append(float(ar['acc_z']))
            if (ar['gyr_x'] is not None):
                w_x.append(float(ar['gyr_x']))
                w_y.append(float(ar['gyr_y']))
                w_z.append(float(ar['gyr_z']))
        else:
            round_t.append(float(ar['timestamp_received']))
            print ar['timestamp_received']
    

    acc_x = np.array(acc_x)
    acc_y = np.array(acc_y)
    acc_z = np.array(acc_z)
    w_x = np.array(w_x)
    w_y = np.array(w_y)
    w_z = np.array(w_z)
    t = np.array(t)-t0
    round_t = np.array(round_t)-t0
    return (t,round_t,acc_x,acc_y,acc_z,w_x,w_y,w_z)
 
t, round_t, acc_x, acc_y, acc_z, w_x, w_y, w_z = get_accel('data/run2/sensor_events.json')
    
print len(acc_x)    

#t = np.arange(len(acc_x))

m_w_x = np.mean(w_x)
m_w_y = np.mean(w_y)
m_w_z = np.mean(w_z)
fig = plt.figure()
host = fig.add_subplot(111)
par1 = host.twinx()    
#par2 = par1.twinx()    
#par3 = par2.twinx()    
    
v_w_x = np.cumsum(w_x-m_w_x)
v_w_y = np.cumsum(w_y-m_w_y)
v_w_z = np.cumsum(w_z-m_w_z)
a = 10    
m_x = np.mean(acc_x)
m_y = np.mean(acc_y)
s_x = np.cumsum(np.cumsum(acc_x-m_x))

ind1 = (w_z[1:]-w_z[:-1]) < -5.

ind2 = ((w_z[1:]-w_z[:-1]) < 1.) & ((w_z[1:]-w_z[:-1]) > 0.) & (w_z[1:] > 500.)

#v_y = np.cumsum(acc_y-m_y)
print round_t
host.plot(t,w_z,'r')
#par2.plot(round_t,np.ones(len(round_t)),'b+')
host.plot(t[1:][ind1],w_z[1:][ind1],'y*')
host.plot(t[1:][ind2],w_z[1:][ind2],'b^')
par1.plot(t,w_x,'g')
#host.plot(t[2:],(w_z[:-2]+w_z[2:])/a,'r')
#par1.plot(t[2:],(w_y[:-2]+w_y[2:])/a)
plt.show()

print m_x
print m_y
var = np.sqrt(np.var(acc_x-m_x, ddof=-2))

print var
#plt.hist(acc_x,bins=30)
#plt.show()

#print acc_x


n = 5
v_x = np.ones(len(acc_x)-n)
v_y = np.ones(len(acc_x)-n)
for i in np.arange(n):
    v_x += acc_x[i:-n+i]
    v_y += acc_y[i:-n+i]
#m, c = np.linalg.lstsq(np.vstack([t, np.ones(len(t))]).T, v_x)[0]

#print m

#v_x = np.cumsum(acc_x-m)-c
#v_x = np.cumsum(acc_x-m)
    
fig = plt.figure()
host = fig.add_subplot(111)
par1 = host.twinx()    
    
host.plot(t[:-n],v_x,'r')
par1.plot(t[:-n],v_y)
#plt.plot(t,v_x1)
#plt.plot(t,v_x2)
plt.show()

acc_x, acc_y, acc_z = get_accel('sample-data/equal.velocity/73167.json')


t = np.arange(len(acc_x))
v_x = np.cumsum(acc_x)


print np.mean(acc_x)
m, c = np.linalg.lstsq(np.vstack([t, np.ones(len(t))]).T, v_x)[0]

#v_x = np.cumsum(acc_x-m)-c
#v_x = np.cumsum(acc_x-m)
    
plt.plot(t,v_x)


#plt.plot(t,acc_z)
plt.show()