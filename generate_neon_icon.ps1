Add-Type -AssemblyName System.Drawing

$bmp = New-Object System.Drawing.Bitmap(512, 512)
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias

# 1. Dark Neon Glowing Background
$rect = New-Object System.Drawing.Rectangle(0, 0, 512, 512)
$brushBg = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    $rect,
    [System.Drawing.Color]::FromArgb(255, 10, 15, 45),
    [System.Drawing.Color]::FromArgb(255, 45, 10, 75),
    45
)

$pathBg = New-Object System.Drawing.Drawing2D.GraphicsPath
$r = 100
$pathBg.AddArc(16, 16, $r, $r, 180, 90)
$pathBg.AddArc(512 - 16 - $r, 16, $r, $r, 270, 90)
$pathBg.AddArc(512 - 16 - $r, 512 - 16 - $r, $r, $r, 0, 90)
$pathBg.AddArc(16, 512 - 16 - $r, $r, $r, 90, 90)
$pathBg.CloseAllFigures()

$g.FillPath($brushBg, $pathBg)

# Glowing Neon Border
$penGlow = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(180, 0, 180, 255), 6)
$g.DrawPath($penGlow, $pathBg)

# 2. Left Cyan/Blue Figure
$rectBlue = New-Object System.Drawing.Rectangle(100, 80, 200, 300)
$brushBlue = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    $rectBlue,
    [System.Drawing.Color]::FromArgb(255, 0, 210, 255),
    [System.Drawing.Color]::FromArgb(255, 0, 100, 255),
    90
)
$g.FillEllipse($brushBlue, 175, 80, 90, 90)

$pathLeft = New-Object System.Drawing.Drawing2D.GraphicsPath
$pathLeft.AddBezier(220, 160, 120, 210, 110, 310, 210, 390)
$pathLeft.AddBezier(210, 390, 270, 350, 260, 300, 220, 160)
$g.FillPath($brushBlue, $pathLeft)

# 3. Right Pink/Purple Figure
$rectPink = New-Object System.Drawing.Rectangle(250, 100, 200, 300)
$brushPink = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    $rectPink,
    [System.Drawing.Color]::FromArgb(255, 255, 120, 220),
    [System.Drawing.Color]::FromArgb(255, 170, 0, 255),
    90
)
$g.FillEllipse($brushPink, 275, 100, 80, 80)

$pathRight = New-Object System.Drawing.Drawing2D.GraphicsPath
$pathRight.AddBezier(305, 170, 395, 210, 395, 310, 305, 390)
$pathRight.AddBezier(305, 390, 250, 350, 250, 300, 305, 170)
$g.FillPath($brushPink, $pathRight)

# 4. Center White Chat Bubble
$brushChat = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(245, 255, 255, 255))
$g.FillEllipse($brushChat, 186, 210, 140, 110)

$p1 = New-Object System.Drawing.Point(210, 305)
$p2 = New-Object System.Drawing.Point(190, 335)
$p3 = New-Object System.Drawing.Point(235, 315)
$pts = [System.Drawing.Point[]]@($p1, $p2, $p3)
$pathTail = New-Object System.Drawing.Drawing2D.GraphicsPath
$pathTail.AddPolygon($pts)
$g.FillPath($brushChat, $pathTail)

# 3 Dots
$brushDots = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 20, 30, 80))
$g.FillEllipse($brushDots, 215, 250, 16, 16)
$g.FillEllipse($brushDots, 248, 250, 16, 16)
$g.FillEllipse($brushDots, 281, 250, 16, 16)

# 5. Bottom Lotus Petals
$rectPetalL = New-Object System.Drawing.Rectangle(80, 320, 180, 150)
$brushPetalL = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    $rectPetalL,
    [System.Drawing.Color]::FromArgb(255, 210, 80, 255),
    [System.Drawing.Color]::FromArgb(255, 0, 180, 255),
    45
)
$pathPetalL = New-Object System.Drawing.Drawing2D.GraphicsPath
$pathPetalL.AddBezier(256, 450, 170, 460, 90, 370, 90, 320)
$pathPetalL.AddBezier(90, 320, 180, 340, 230, 390, 256, 450)
$g.FillPath($brushPetalL, $pathPetalL)

$rectPetalR = New-Object System.Drawing.Rectangle(250, 320, 180, 150)
$brushPetalR = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    $rectPetalR,
    [System.Drawing.Color]::FromArgb(255, 0, 180, 255),
    [System.Drawing.Color]::FromArgb(255, 210, 80, 255),
    135
)
$pathPetalR = New-Object System.Drawing.Drawing2D.GraphicsPath
$pathPetalR.AddBezier(256, 450, 342, 460, 422, 370, 422, 320)
$pathPetalR.AddBezier(422, 320, 332, 340, 282, 390, 256, 450)
$g.FillPath($brushPetalR, $pathPetalR)

# Save images
$bmp.Save("D:\Saathi\app_icon_512.png", [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Save("D:\Saathi\app\src\main\res\mipmap-xxxhdpi\ic_launcher_512.png", [System.Drawing.Imaging.ImageFormat]::Png)

$g.Dispose()
$bmp.Dispose()
Write-Host "SUCCESS: 512x512 Neon Embracing App Icon generated at D:\Saathi\app_icon_512.png"
