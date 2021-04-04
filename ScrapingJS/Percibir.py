from tkinter import Image

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time
from PIL import Image, ImageOps
from os import remove
import os
DRIVER_PATH = 'C:\chromedriver\chromedriver.exe'


options = Options()
options.headless = True
options.add_argument("--window-size=1920,1200")

driver = webdriver.Chrome(options=options, executable_path=DRIVER_PATH)
#driver.get("https://www.nintendo.com/")
driver.get("https://www.juegosinfantilespum.com/laberintos-online/12-auto-buhos.php")

time.sleep(10)
element = driver.find_element_by_xpath("/html/body/div/div[1]/div[1]/div[1]/canvas")
element.click()


driver.set_window_size(639,321) # May need manual adjustment 639,325
driver.find_element_by_tag_name('canvas').screenshot('web_screenshot.png')


driver.quit()

#os.getcwd()


img = Image.open("web_screenshot.png")
border = (7, 31, 0, 0) # left, up, right, bottom
newIMG = ImageOps.crop(img, border)
newIMG.save("nivel1.png")
newIMG.show()


remove(os.getcwd() + '\web_screenshot.png')




