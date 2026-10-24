Add-Type -AssemblyName System.Drawing

$bmp = New-Object System.Drawing.Bitmap(512, 512)
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias

# Soft Blue Background
$brushBg = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 227, 242, 253))
$g.FillRectangle($brushBg, 0, 0, 512, 512)

# White Inner Circle
$brushCircle = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
$g.FillEllipse($brushCircle, 56, 56, 400, 400)

# Red Heart
$brushHeart = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 107, 107))
$path = New-Object System.Drawing.Drawing2D.GraphicsPath
$path.AddBezier(256, 370, 160, 290, 120, 200, 190, 150)
$path.AddBezier(190, 150, 240, 150, 256, 190, 256, 190)
$path.AddBezier(256, 190, 272, 150, 322, 150, 322, 150)
$path.AddBezier(322, 150, 392, 200, 352, 290, 256, 370)
$g.FillPath($brushHeart, $path)

$bmp.Save("D:\Saathi\app_icon_512.png", [System.Drawing.Imaging.ImageFormat]::Png)
$g.Dispose()
$bmp.Dispose()
Write-Host "SUCCESS: 512x512 App Icon generated at D:\Saathi\app_icon_512.png"
